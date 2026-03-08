package com.impacsys.core.common.server.controller;

import com.impacsys.core.common.command.AbstractTypeCommand;
import com.impacsys.core.common.dto.AbstractDto;
import com.impacsys.core.common.dto.AbstractTypeDto;
import com.impacsys.core.common.entity.AbstractTypeEntity;

/**
 *
 * @author hkb@imcorp.kr
 * @since 2025.7.9
 *
 * @param <C>
 * @param <E>
 * @param <D>
 */
public abstract class AbstractTypeController<C extends AbstractTypeCommand, E extends AbstractTypeEntity, D extends AbstractTypeDto>
		extends AbstractCommonController<C, E, AbstractDto<E>> {

}
