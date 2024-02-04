import { IBooks } from 'app/shared/model/books.model';

export interface IAuthor {
  id?: number;
  name?: string;
  books?: IBooks[] | null;
}

export const defaultValue: Readonly<IAuthor> = {};
