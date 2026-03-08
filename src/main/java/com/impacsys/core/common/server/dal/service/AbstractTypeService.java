package com.impacsys.core.common.server.dal.service;

import java.util.Objects;

import com.impacsys.core.common.dto.AbstractDto;
import com.impacsys.core.common.dto.AbstractTypeDto;
import com.impacsys.core.common.entity.AbstractTypeEntity;
import com.impacsys.core.common.entity.EntityAdoptor;

/**
 * Represents basic CRUD based on each type related tables.
 *
 * @author hkb@imcorp.kr
 * @since 2025.7.9
 *
 * @param <E>
 * @param <D>
 */
public abstract class AbstractTypeService<E extends AbstractTypeEntity, D extends AbstractTypeDto>
		extends AbstractCommonService<E, AbstractDto<E>> {

	@Override
	public EntityAdoptor<E> getEntityAdoptor() {

		return (existingOne, newOne) -> {

			if (Objects.nonNull(newOne.getName())) {
				existingOne.setName(newOne.getName());
			}

			if (Objects.nonNull(newOne.getDescription())) {
				existingOne.setDescription(newOne.getDescription());
			}
		};
	}

}
