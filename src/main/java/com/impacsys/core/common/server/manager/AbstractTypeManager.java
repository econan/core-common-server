package com.impacsys.core.common.server.manager;

import com.impacsys.core.common.command.AbstractCommonCommand;
import com.impacsys.core.common.command.AbstractTypeCommand;
import com.impacsys.core.common.dto.AbstractDto;
import com.impacsys.core.common.dto.AbstractTypeDto;
import com.impacsys.core.common.dto.mapper.CommonEntityMapper;
import com.impacsys.core.common.entity.AbstractTypeEntity;
import com.impacsys.core.common.server.dal.service.AbstractCommonService;
import com.impacsys.core.common.server.dal.service.AbstractTypeService;

/**
 *
 * @author hkb@imcorp.kr
 * @since 2025.7.9
 *
 * @param <C>
 * @param <E>
 * @param <D>
 */
public abstract class AbstractTypeManager<C extends AbstractCommonCommand, E extends AbstractTypeEntity, D extends AbstractTypeDto>
		extends AbstractCommonManager<C, E, AbstractDto<E>> {

	protected final AbstractTypeService<E, D> typeService;
    
    protected AbstractTypeManager(final AbstractTypeService<E, D> typeService) {
        this.typeService = typeService;
    }

    @Override
    protected final AbstractCommonService<E, AbstractDto<E>> service() {
        return (AbstractCommonService<E, AbstractDto<E>>) typeService;
    }

	@Override
	public final CommonEntityMapper<C, E> getCommonEntityMapper() {
		return (command) -> {
			final AbstractTypeCommand typeCommand = (AbstractTypeCommand) command;
			try {
				E entity = getEntityClass().getDeclaredConstructor().newInstance();
				entity.setId(typeCommand.getId());
				entity.setStatus(typeCommand.getStatus());
				entity.setName(typeCommand.getName());
				entity.setDescription(typeCommand.getDescription());
				return entity;
			} catch (Exception e) {
				throw new RuntimeException("Failed to create entity instance", e);
			}
		};
	}

	/**
	 * Returns the class of the entity managed by this manager.
	 * 
	 * @return
	 */
	protected abstract Class<E> getEntityClass();
			
}
