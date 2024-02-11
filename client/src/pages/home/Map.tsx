import Button from '@/components/Button';
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

  useEffect(() => {
    const center: naver.maps.LatLng = new naver.maps.LatLng(37.3595704, 127.105399);

    const map = new naver.maps.Map('map', {
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
          return alert('Something Wrong!');
        }

        if (response.v2.meta.totalCount === 0) {
          return alert('totalCount' + response.v2.meta.totalCount);
        }

        const item = response.v2.addresses[0];
        setItem(item);
      },
    );
  };

  return (
    <div className="z-10 absolute top-[68px] rounded-xl bg-white p-4">
      <div className="pb-2">
        <input
          ref={searchAddress}
          className="cursor-default rounded-md bg-white py-1.5 px-3 text-left shadow-sm ring-1 ring-inset ring-gray-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 sm:text-sm sm:leading-6"
          placeholder="검색할 주소"></input>
        <Button text="검색" onClick={() => searchAddressToCoordinate(searchAddress.current ? searchAddress.current.value : '')} />
      </div>
      <div id="map" ref={mapElement} className="w-[500px] h-[500px] p-2" />
    </div>
  );
}

export default Map;

