package com.impacsys.core.common.server.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.impacsys.core.common.command.AbstractCommonCommand;
import com.impacsys.core.common.command.validation.CommonValidationConstants;
import com.impacsys.core.common.command.validation.exception.CommandValidationError;
import com.impacsys.core.common.command.validation.exception.CommandValidationException;
import com.impacsys.core.common.dto.AbstractDto;
import com.impacsys.core.common.entity.AbstractCommonEntity;
import com.impacsys.core.common.server.manager.AbstractCommonManager;

/**
 * To test {@link AbstractCommonController}
 *
 * @author hkb@imcorp.kr
 * @since 2025.7.9
 *
 */
public class AbstractCommonControllerTest {

	@Mock
	private AbstractCommonManager<AbstractCommonCommand, AbstractCommonEntity, AbstractDto<AbstractCommonEntity>> manager;

	private final AbstractCommonController<AbstractCommonCommand, AbstractCommonEntity, AbstractDto<AbstractCommonEntity>> controller =
			new AbstractCommonController<AbstractCommonCommand, AbstractCommonEntity, AbstractDto<AbstractCommonEntity>>() {

		@Override
		public AbstractCommonManager<AbstractCommonCommand, AbstractCommonEntity, AbstractDto<AbstractCommonEntity>> getManager() {

			return AbstractCommonControllerTest.this.manager;
		}

	};

	@Test
	public void test_update_invalidId() {

		final TestCommand command = new TestCommand();
		command.setId(10L);
		try {
			this.controller.update(1, command);
			fail("Must be failed");

		} catch (final CommandValidationException e) {

			final List<CommandValidationError> errors = e.getFieldErrors();
			assertEquals(true, errors.size() == 1);
			assertEquals(true, errors.get(0).getErrorField().equals(CommonValidationConstants.FieldName.ID));
			assertEquals(true, errors.get(0).getErrorCode().equals(CommonValidationConstants.ErrorCode.ERROR_INVALID));
		}
	}


	class TestCommand extends AbstractCommonCommand {

	}
}
