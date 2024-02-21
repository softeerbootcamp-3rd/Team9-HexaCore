import { TargetType } from "@/components/ListComponent";
import { server } from "../common/axios";
import { ResponseWithoutData } from "../common/response.type";
import { ReviewData } from "./Review.type";

export const fetchReviews = async (reviewData: ReviewData, type: TargetType) => {
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
}
