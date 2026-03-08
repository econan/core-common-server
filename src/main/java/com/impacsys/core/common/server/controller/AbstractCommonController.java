package com.impacsys.core.common.server.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.impacsys.core.common.command.AbstractCommonCommand;
import com.impacsys.core.common.command.validation.CommonValidationConstants;
import com.impacsys.core.common.command.validation.group.DefaultValidationGroup.Insert;
import com.impacsys.core.common.command.validation.group.DefaultValidationGroup.Select;
import com.impacsys.core.common.command.validation.group.DefaultValidationGroup.Update;
import com.impacsys.core.common.command.validation.utils.CommandValidationExceptionUtil;
import com.impacsys.core.common.dto.AbstractDto;
import com.impacsys.core.common.entity.AbstractCommonEntity;
import com.impacsys.core.common.server.aspect.group.LoggingRequestAndResponse;
import com.impacsys.core.common.server.manager.AbstractCommonManager;

import jakarta.validation.constraints.Min;

/**
 * Represents Basic CRUD requests for each end-point.
 *
 * For unit test {#link AbstractCommonControllerTest}
 *
 * @author hkb@imcorp.kr
 * @since 2025.7.9
 *
 * @param <C>
 * @param <E>
 * @param <D>
 */
public abstract class AbstractCommonController<C extends AbstractCommonCommand, E extends AbstractCommonEntity, D extends AbstractDto<E>> {

	/**
	 * Represents to Insert <code>E extends AbstractCommonEntity</code> by given <code>C extends AbstractCommonCommand</code>.
	 * TODO: Consider to support bulk insert.
	 * 
	 * @param command
	 * @return
	 */
	@LoggingRequestAndResponse
	@PostMapping
	public ResponseEntity<D> insert(@RequestBody @Validated({Insert.class}) final C command) {

		return ResponseEntity.ok(
				this.getManager().insert(command).orElse(null));
	}

	/**
	 * Represents to Update <code>E extends AbstractCommonEntity</code> by given <code>C extends AbstractCommonCommand</code>.
	 *
	 * @param id
	 * @param command
	 * @return
	 */
	@LoggingRequestAndResponse
	@PutMapping(CommonMvcMapping.Url.PATH_ID)
	public ResponseEntity<Void> update(
			@PathVariable(value = CommonMvcMapping.ModelKey.ID, required = true)
			@Min(value = 1L, message = CommonValidationConstants.ErrorCode.ERROR_LESS_MIN) final long id,
			@RequestBody @Validated({Update.class}) final C command) {

		// ... validate id which is must be matched with id in command
		if (!Long.valueOf(id).equals(command.getId())) {

			CommandValidationExceptionUtil.throwCommandValidationException(
					this.getClass().getCanonicalName() + ".update()",
					CommonValidationConstants.FieldName.ID,
					CommonValidationConstants.ErrorCode.ERROR_INVALID,
					command);
		}

		if (!this.getManager().update(command)) {

			CommandValidationExceptionUtil.throwCommandValidationException(
					this.getClass().getCanonicalName() + ".update()",
					CommonValidationConstants.FieldName.ID,
					CommonValidationConstants.ErrorCode.ERROR_NOTFOUND,
					command);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Represents to Delete <code>E extends AbstractCommonEntity</code> by given <code>C extends AbstractCommonCommand</code>.
	 *
	 * @param id
	 * @return
	 */
	@LoggingRequestAndResponse
	@DeleteMapping(CommonMvcMapping.Url.PATH_ID)
	public ResponseEntity<Void> delete(
			@PathVariable(value = CommonMvcMapping.ModelKey.ID, required = true)
			@Min(value = 1L, message = CommonValidationConstants.ErrorCode.ERROR_LESS_MIN) final long id) {

		if (!this.getManager().updateAsDeleted(id)) {

			CommandValidationExceptionUtil.throwCommandValidationException(
					this.getClass().getCanonicalName() + ".delete()",
					CommonValidationConstants.FieldName.ID,
					CommonValidationConstants.ErrorCode.ERROR_NOTFOUND,
					null);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Represents to Update <code>E extends AbstractCommonEntity</code> by given <code>C extends AbstractCommonCommand</code>.
	 *
	 * @param id
	 * @return
	 */
	@LoggingRequestAndResponse
	@GetMapping(CommonMvcMapping.Url.PATH_ID)
	public ResponseEntity<D> selectById(
			@PathVariable(value = CommonMvcMapping.ModelKey.ID, required = true)
			@Min(value = 1L, message = CommonValidationConstants.ErrorCode.ERROR_LESS_MIN) final long id) {

		return ResponseEntity.ok(this.getManager().selectById(id).orElse(null));
	}

	/**
	 * Represents to Paging the list of <code>E extends AbstractCommonEntity</code> by given <code>C extends AbstractCommonCommand</code>.
	 *
	 * @param command
	 * @return
	 */
	@LoggingRequestAndResponse
	@GetMapping
	public ResponseEntity<Page<D>> select(@Validated({Select.class}) final C command) {

		final Pageable pageable = PageRequest.of(
				command.getPage(),
				command.getSize(),
				Sort.by(command.getOrders()));

		return ResponseEntity.ok(
				this.getManager().pagingWithExample(command, pageable));
	}

	/**
	 * Please override for each controller to provide a manager.
	 *
	 * @return
	 */
	public abstract AbstractCommonManager<C, E, D> getManager();

}
