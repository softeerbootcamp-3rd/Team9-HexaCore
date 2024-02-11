export type Reservation = {
  id: number | undefined;
  target: { id: number | undefined; name: string | null; image: string | null; phoneNumber: string | null };
  fee: number | undefined;
  address: string | null;
  startDate: string | null;
  endDate: string | null;
  status: string | null;
};