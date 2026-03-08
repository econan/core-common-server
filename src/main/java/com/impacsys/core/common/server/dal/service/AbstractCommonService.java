package com.impacsys.core.common.server.dal.service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.ImmutableList;
import com.impacsys.core.common.dto.AbstractDto;
import com.impacsys.core.common.entity.AbstractCommonEntity;
import com.impacsys.core.common.entity.EntityAdoptor;
import com.impacsys.core.common.server.dal.repository.DefaultJpaRepository;

import jakarta.annotation.Nonnull;

/**
 * Represents basic CRUD based on each table. For more complex queries, override
 * each methods to handle business logic.
 *
 * @author hkb@imcorp.kr
 * @since 2025.7.9
 *
 * @param <E>
 * @param <D>
 */
public abstract class AbstractCommonService<E extends AbstractCommonEntity, D extends AbstractDto<E>> {

	/**
	 * Selects an <code>Optional<E></code> by other search options instead of id.
	 *
	 * TODO: select by id only? what kind of options to search an Entity?
	 *
	 * @param entity
	 * @return
	 */
	@Transactional(readOnly = true)
	public Optional<D> select(@Nonnull final E entity) {

		final Optional<E> selected = this.getRepository().findOne(Example.of(entity));

		return (selected.isEmpty()) ?
				Optional.empty() : Optional.of(this.toDto(selected.get()));
	}

	/**
	 * Selects an <code>Optional<E></code> by id.
	 *
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public Optional<D> selectById(@Nonnull final long id) {

		final Optional<E> selected = this.getRepository().findById(id);
		return selected.isEmpty() ?
				Optional.empty() : Optional.of(this.toDto(selected.get()));
	}

	/**
	 * Selects all list of <code>E</cod> by given ids
	 *
	 * @param ids
	 * @return
	 */
	@Transactional(readOnly = true)
	public ImmutableList<D> findAllById(@Nonnull final Set<Long> ids) {

		final List<E> selected = this.getRepository().findAllById(ids);

		return CollectionUtils.isEmpty(selected) ?
				ImmutableList.of() : ImmutableList.copyOf(this.toDtos(selected));
	}

	/**
	 * Search by given conditions.
	 *
	 * @param entity
	 * @return
	 */
	@Transactional(readOnly = true)
	public ImmutableList<D> selectAll(@Nonnull final E entity) {

		final List<E> selected = this.getRepository().findAll(Example.of(entity));

		return CollectionUtils.isEmpty(selected) ?
				ImmutableList.of() : ImmutableList.copyOf(this.toDtos(selected));
	}

	/**
	 *
	 * @param example
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<D> pagingWithExample(@Nonnull final Example<E> example, @Nonnull final Pageable pageable) {

		return this.toDtoPage(this.getRepository().findAll(example, pageable));
	}

	/**
	 * Inserts given entity.
	 *
	 * @param entity
	 * @return
	 */
	@Transactional(readOnly = false)
	public Optional<D> insert(@Nonnull final E entity) {

		final E inserted = this.getRepository().save(entity);

		return Objects.isNull(inserted) ?
				Optional.empty() : Optional.of(this.toDto(inserted));

	}

	/**
	 * Inserts list of <code>List<E> entities</code>.
	 *
	 * @param entities
	 */
	@Transactional(readOnly = false)
	public ImmutableList<D> saveAll(@Nonnull final List<E> entities) {

		final List<E> inserted = this.getRepository().saveAll(entities);

		return CollectionUtils.isEmpty(inserted) ?
				ImmutableList.of() : ImmutableList.copyOf(this.toDtos(inserted));
	}

	/**
	 * Inserts and flush list of <code>List<E> entities</code>.
	 *
	 * @param entities
	 */
	@Transactional(readOnly = false)
	public ImmutableList<D> saveAllAndFlush(@Nonnull final List<E> entities) {

		final List<E> inserted = this.getRepository().saveAllAndFlush(entities);

		return CollectionUtils.isEmpty(inserted) ?
				ImmutableList.of() : ImmutableList.copyOf(this.toDtos(inserted));
	}

	/**
	 * Updates given entity. If not existing, Exception will be thrown.
	 *
	 * @param entity
	 * @return
	 */
	@Transactional(readOnly = false)
	public boolean update(@Nonnull final E entity) {

		final Optional<E> result = (this.getRepository().findById(entity.getId()));
		if (result.isEmpty()) {
			return false;
		}

		final E selectedEntity = result.get();

		// ... set new value in existing entity to save
		this.getEntityAdoptor().setToUpdate(selectedEntity, entity);

		return Objects.nonNull(this.getRepository().save(selectedEntity));
	}

	/**
	 * Delete a row by given <code>E entity</code>.
	 *
	 * @param entity
	 * @return
	 */
	@Transactional(readOnly = false)
	public boolean deleteRow(@Nonnull final E entity) {

		this.getRepository().deleteById(entity.getId());

		return true;
	}

	/**
	 * Delete a row by given id.
	 *
	 * NOTE: Please considering delete a row.
	 *
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = false)
	public boolean deleteRow(final long id) {

		final Optional<E> result = (this.getRepository().findById(id));
		if (result.isEmpty()) {
			return false;
		}

		this.getRepository().deleteById(id);

		return true;
	}

	/**
	 * Updates a status as deleted (status = 0). If not existing, Exception will be
	 * thrown.
	 *
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = false)
	public boolean updateAsDeleted(final long id) {

		final Optional<E> result = (this.getRepository().findById(id));
		if (result.isEmpty()) {
			return false;
		}

		final E selectedEntity = result.get();
		selectedEntity.setStatus(AbstractCommonEntity.STATUS_DELETE);

		return Objects.nonNull(this.getRepository().save(selectedEntity));
	}

	/**
	 * Check whether given <code>Example<E> is existing or not.
	 *
	 * @param example
	 * @return
	 */
	@Transactional(readOnly = true)
	public boolean exists(@Nonnull final Example<E> example) {

		return this.getRepository().exists(example);
	}

	/**
	 * Convert Collection<E> to List<D>
	 *
	 * @param entities
	 * @return
	 */
	public List<D> toDtos(@Nonnull final Collection<E> entities) {

		return entities.stream().map(entity -> this.toDto(entity)).toList();
	}

	/**
	 * Convert Page<E> to Page<D>
	 *
	 * @param page
	 * @return
	 */
	public Page<D> toDtoPage(@Nonnull final Page<E> page) {

		return page.map(entity -> this.toDto(entity));
	}

	/**
	 * TODO: Should be in manager instead of service?
	 * @return
	 */
	public abstract EntityAdoptor<E> getEntityAdoptor();

	/**
	 *
	 * @return
	 */
	public abstract DefaultJpaRepository<E, Long> getRepository();

	/**
	 * Convert entity to dto
	 *
	 * @param entity
	 * @return
	 */
	public abstract D toDto(E entity);
}
