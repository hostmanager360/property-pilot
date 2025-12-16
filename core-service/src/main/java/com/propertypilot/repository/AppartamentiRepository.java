package com.propertypilot.repository;

import com.propertypilot.model.Appartamento;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
public class AppartamentiRepository implements JpaRepository<Appartamento, Integer> {

    @Override
    public void flush() {

    }

    @Override
    public <S extends Appartamento> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Appartamento> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Appartamento> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Integer> integers) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Appartamento getOne(Integer integer) {
        return null;
    }

    @Override
    public Appartamento getById(Integer integer) {
        return null;
    }

    @Override
    public Appartamento getReferenceById(Integer integer) {
        return null;
    }

    @Override
    public <S extends Appartamento> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Appartamento> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Appartamento> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Appartamento> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Appartamento> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Appartamento> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Appartamento, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Appartamento> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Appartamento> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Appartamento> findById(Integer integer) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Integer integer) {
        return false;
    }

    @Override
    public List<Appartamento> findAll() {
        return List.of();
    }

    @Override
    public List<Appartamento> findAllById(Iterable<Integer> integers) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Integer integer) {

    }

    @Override
    public void delete(Appartamento entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> integers) {

    }

    @Override
    public void deleteAll(Iterable<? extends Appartamento> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Appartamento> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Appartamento> findAll(Pageable pageable) {
        return null;
    }
}
