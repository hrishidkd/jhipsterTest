import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Books from './books';
import BooksDetail from './books-detail';
import BooksUpdate from './books-update';
import BooksDeleteDialog from './books-delete-dialog';

const BooksRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Books />} />
    <Route path="new" element={<BooksUpdate />} />
    <Route path=":id">
      <Route index element={<BooksDetail />} />
      <Route path="edit" element={<BooksUpdate />} />
      <Route path="delete" element={<BooksDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default BooksRoutes;
