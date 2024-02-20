import { server } from "../common/axios";
import { ResponseWithData, ResponseWithoutData } from "../common/response.type";
import { NotificationData } from "./notification.type";

export const getNotifications = async () => {
  const response = await server.get<ResponseWithData<NotificationData[]>>('/notifications');

  if (response.success) {
    console.log(response);
    if (response === undefined) {
        return [];
    } else {
        return response.data;
    }
  } else {
    alert(response.message)
  }
};

export const deleteAllNotifications = async () => {
  const response = await server.delete<ResponseWithoutData>('/notifications');

  if (response.success) {
    console.log(response);
  } else {
    alert(response.message)
  }
};
  