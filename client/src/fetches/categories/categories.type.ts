export type CategoryResponse = {
  id: number;
  name: string;
  subcategories: Category[];
};

export type Category = {
  id: number;
  name: string;
};

