import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './books.reducer';

export const BooksDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const booksEntity = useAppSelector(state => state.books.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="booksDetailsHeading">Books</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{booksEntity.id}</dd>
          <dt>
            <span id="title">Title</span>
          </dt>
          <dd>{booksEntity.title}</dd>
          <dt>
            <span id="price">Price</span>
          </dt>
          <dd>{booksEntity.price}</dd>
          <dt>Author</dt>
          <dd>{booksEntity.author ? booksEntity.author.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/books" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/books/${booksEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default BooksDetail;
