import { IAuthor } from 'app/shared/model/author.model';

export interface IBooks {
  id?: number;
  title?: string;
  price?: number;
  author?: IAuthor;
}

export const defaultValue: Readonly<IBooks> = {};
