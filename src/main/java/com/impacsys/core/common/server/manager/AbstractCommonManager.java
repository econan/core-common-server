package com.impacsys.core.common.server.manager;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.google.common.collect.ImmutableList;
import com.impacsys.core.common.command.AbstractCommonCommand;
import com.impacsys.core.common.dto.AbstractDto;
import com.impacsys.core.common.dto.mapper.CommonEntityMapper;
import com.impacsys.core.common.entity.AbstractCommonEntity;
import com.impacsys.core.common.server.dal.service.AbstractCommonService;

import jakarta.annotation.Nonnull;

/**
 * AbstractCommonManager provides common methods for managing entities
 * in a service layer. It defines methods for selecting, inserting, updating,
 * and deleting entities, as well as for pagination and searching by example.
 *
 * NOTE: Complex business logic should be done in here instead of
 * in the controller and service layer.
 * 
 * @author hkb@imcorp.kr
 * @since 2025.7.9
 *
 * @param <C>
 * @param <E>
 * @param <D>
 */
public abstract class AbstractCommonManager<C extends AbstractCommonCommand, E extends AbstractCommonEntity, D extends AbstractDto<E>> {

	/**
	 * Searches an <code>Optional<E></code> by given command.
	 *
	 * @param command
	 * @return
	 */
	public Optional<D> select(@Nonnull final C command) {

		return this.service().select(this.getCommonEntityMapper().toEntity(command));
	}

	/**
	 * Selects an <code>Optional<E></code> by id.
	 *
	 * @param id
	 * @return
	 */
	public Optional<D> selectById(final long id) {

		return this.service().selectById(id);
	}

	/**
	 * Search by given conditions.
	 *
	 * @param command
	 * @return
	 */
	public ImmutableList<D> selectAll(@Nonnull final C command) {

		return this.service().selectAll(this.getCommonEntityMapper().toEntity(command));
	}

	/**
	 * Selects all list of <code>E</cod> by given ids
	 *
	 * @param ids
	 * @return
	 */
	public ImmutableList<D> findAllById(@Nonnull final Set<Long> ids) {

		return this.service().findAllById(ids);
	}

	/**
	 *
	 * @param command
	 * @param pageable
	 * @return
	 */
	public Page<D> pagingWithExample(@Nonnull final C command, @Nonnull final Pageable pageable) {

		return this.service().pagingWithExample(
			Example.of(this.getCommonEntityMapper().toEntity(command)), pageable);
	}

	/**
	 * Inserts given command.
	 *
	 * @param command
	 * @return
	 */
	public Optional<D> insert(@Nonnull final C command) {

		return this.service().insert(this.getCommonEntityMapper().toEntity(command));
	}

	/**
	 * Updates given command. If not existing, Exception will be thrown.
	 *
	 * @param command
	 * @return
	 */
	public boolean update(@Nonnull final C command) {

		return this.service().update(this.getCommonEntityMapper().toEntity(command));
	}

	/**
	 * Delete a row by given <code>C command</code>.
	 *
	 * @param command
	 * @return
	 */
	public boolean deleteRow(@Nonnull final C command) {

		return this.service().deleteRow(this.getCommonEntityMapper().toEntity(command));
	}

	/**
	 * Delete a row by given id.
	 *
	 * NOTE: Please considering delete a row.
	 *
	 * @param id
	 * @return
	 */
	public boolean deleteRow(final long id) {

		return this.service().deleteRow(id);
	}

	/**
	 * Updates a status as deleted (status = 0). If not existing, Exception will be
	 * thrown.
	 *
	 * @param id
	 * @return
	 */
	public boolean updateAsDeleted(final long id) {

		return this.service().updateAsDeleted(id);
	}

	/**
	 * Check whether given <code>Example<E> is existing or not.
	 *
	 * @param example
	 * @return
	 */
	public boolean exists(@Nonnull final C command) {

		return this.service().exists(
			Example.of(this.getCommonEntityMapper().toEntity(command), null));
	}

	protected abstract AbstractCommonService<E, D> service();

	/**
	 *
	 * @return
	 */
	public abstract CommonEntityMapper<C, E> getCommonEntityMapper();
}
