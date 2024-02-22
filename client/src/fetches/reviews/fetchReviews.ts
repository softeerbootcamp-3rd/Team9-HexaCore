import { TargetType } from "@/components/ListComponent";
import { server } from "../common/axios";
import { ResponseWithPagination, ResponseWithoutData } from "../common/response.type";
import { CarReviewResponse, ReviewData } from "./Review.type";

export const fetchCreateReview = async (reviewData: ReviewData, type: TargetType) => {
	if(type === 'guest') {
		const response = await server.post<ResponseWithoutData>('/reviews/car', {
			data: reviewData
		});
		return response;
	} else {
		const response = await server.post<ResponseWithoutData>('/reviews/guest', {
			data: reviewData
		});
		return response;
	}
};

export const fetchCarReviews = async (carId: number, page: number, size: number, sort: string) => {
	const response = await server.get<ResponseWithPagination<CarReviewResponse[]>>(
		'/reviews/car/'+carId+'?page='+page+'&size='+size+'&sort='+sort);
	if(response.success) {
		return response;
	}
}
