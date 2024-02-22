export type ReviewData = {
	reservationId: number;
	contents: string;
	rate: number;
};

export type CarReviewResponse = {
	id: number;
	writer: {
		name: string;
		profileImgUrl: string;
	};
	contents: string;
	rate: number;
}