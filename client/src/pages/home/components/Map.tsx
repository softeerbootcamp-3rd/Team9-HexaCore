import Button from '@/components/Button';
import { useCustomToast } from '@/components/Toast';
import { Dispatch, useEffect, useRef, useState } from 'react';

type MapProps = {
  setAddress: Dispatch<React.SetStateAction<string>>;
  latitude: React.MutableRefObject<number>;
  longitude: React.MutableRefObject<number>;
};

function Map({ setAddress, latitude, longitude }: MapProps) {
  const mapElement = useRef<HTMLDivElement | null>(null);
  const searchAddress = useRef<HTMLInputElement | null>(null);
  const [item, setItem] = useState<naver.maps.Service.AddressItemV2>();
  const { ToastComponent, showToast } = useCustomToast();

  useEffect(() => {
    const center: naver.maps.LatLng = new naver.maps.LatLng(37.3595704, 127.105399);

    new naver.maps.Map('map', {
      center: center,
      zoom: 16,
      mapTypeControl: true,
    });
  }, []);

  useEffect(() => {
    if (item) {
      const longitudeX = Number(item.x);
      const latitudeY = Number(item.y);

      latitude.current = latitudeY;
      longitude.current = longitudeX;

      const center: naver.maps.LatLng = new naver.maps.LatLng(latitudeY, longitudeX);

      const map = new naver.maps.Map('map', {
        center: center,
        zoom: 16,
        mapTypeControl: true,
      });

      const htmlAddresses: string[] = [];
      if (item.roadAddress) {
        htmlAddresses.push('[도로명 주소] ' + item.roadAddress);
        setAddress(item.roadAddress);
      }

      if (item.jibunAddress) {
        htmlAddresses.push('[지번 주소] ' + item.jibunAddress);
      }

      if (item.englishAddress) {
        htmlAddresses.push('[영문명 주소] ' + item.englishAddress);
      }

      const infoWindow = new naver.maps.InfoWindow({
        content: [
          '<div style="padding:10px;min-width:200px;line-height:150%;">',
          '<h4 style="margin-top:5px;">검색 주소 : ' + searchAddress.current?.value + '</h4><br />',
          htmlAddresses.join('<br />'),
          '</div>',
        ].join('\n'),
        anchorSkew: true,
      });
      infoWindow.open(map, new naver.maps.Point(longitudeX, latitudeY));
    }
  }, [item]);

  const searchAddressToCoordinate = (address: string) => {
    naver.maps.Service.geocode(
      {
        query: address,
      },
      function (status, response) {
        if (status === naver.maps.Service.Status.ERROR) {
          showToast('에러 발생', 'NAVER API 에러가 발생했습니다.');
          return;
        }

        if (response.v2.meta.totalCount === 0) {
          showToast('에러 발생', '주소 검색 결과가 존재하지 않습니다.');
          return;
        }

        const item = response.v2.addresses[0];
        setItem(item);
      },
    );
  };

  return (
    <div>
      <div className='pb-2'>
        <input
          ref={searchAddress}
          className='ring-gray-300 focus:ring-indigo-500 cursor-default rounded-md bg-white px-3 py-1.5 text-left shadow-sm ring-1 ring-inset focus:outline-none focus:ring-2 sm:text-sm sm:leading-6'
          placeholder='검색할 주소'></input>
        <Button text='검색' onClick={() => searchAddressToCoordinate(searchAddress.current ? searchAddress.current.value : '')} />
      </div>
      <div id='map' ref={mapElement} className='h-[500px] w-[500px] p-2' />
      <ToastComponent />
    </div>
  );
}

export default Map;

