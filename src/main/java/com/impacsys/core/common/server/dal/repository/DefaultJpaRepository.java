package com.impacsys.core.common.server.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.impacsys.core.common.entity.AbstractCommonEntity;

/**
 * TODO: create custom repository instead of extending Spring <code>JpaRepository</code>?
 *
 * @author hkb@imcorp.kr
 * @since 2025.7.9
 *
 * @param <E>
 * @param <ID>
 */
@NoRepositoryBean
public interface DefaultJpaRepository<E extends AbstractCommonEntity, ID> extends JpaRepository<E, Long> {

}
