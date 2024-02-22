import { Category } from '@/fetches/categories/categories.type';
import { Dispatch, useEffect, useRef, useState } from 'react';

type SubCategorySelectProps = {
  categoryList: Category[];
  selectedSubcategory: Category[];
  setSelectedSubcategory: Dispatch<React.SetStateAction<Category[]>>;
};

function MultipleSelect({ categoryList, selectedSubcategory, setSelectedSubcategory }: SubCategorySelectProps) {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const dropDownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    // 드롭다운 외부 클릭 시 isOpen을 false로 설정
    function handleClickOutside(event: MouseEvent) {
      if (dropDownRef.current && !dropDownRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [dropDownRef]);

  const isOptionSelected = (option: Category) => {
    return selectedSubcategory.includes(option);
  };

  const selectOption = (category: Category) => {
    setSelectedSubcategory((prevSelectedSubcategory) => {
      const index = prevSelectedSubcategory.indexOf(category);

      if (index === -1) {
        return [...prevSelectedSubcategory, category];
      } else {
        return prevSelectedSubcategory.filter((subcategory) => subcategory !== category);
      }
    });
  };

  return (
    <div ref={dropDownRef}>
      <div className="relative mt-2">
        <button
          type="button"
          className="text-gray-900 ring-gray-300 focus:ring-indigo-500 relative min-h-[45px] w-full cursor-default rounded-md bg-white py-1.5 pl-3 pr-10 text-left shadow-sm ring-1 ring-inset focus:outline-none focus:ring-2 sm:text-sm sm:leading-6"
          aria-haspopup="listbox"
          aria-expanded="true"
          aria-labelledby="listbox-label"
          onClick={() => setIsOpen((prev) => !prev)}>
          <div className="overflow-auto whitespace-nowrap">
            <span className="flex items-center gap-1">
              {selectedSubcategory.map((category) => (
                <button
                  key={category.id}
                  className="rounded-md bg-primary-100 px-2 py-1"
                  onClick={(e) => {
                    e.stopPropagation();
                    selectOption(category);
                  }}>
                  {category.name}
                  <span className="text-slate-500 pl-1 text-lg">&times;</span>
                </button>
              ))}
            </span>
          </div>

          <span className="pointer-events-none absolute inset-y-0 right-0 ml-3 flex items-center pr-2">
            <svg className="text-gray-400 h-5 w-5" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
              <path
                fillRule="evenodd"
                d="M10 3a.75.75 0 01.55.24l3.25 3.5a.75.75 0 11-1.1 1.02L10 4.852 7.3 7.76a.75.75 0 01-1.1-1.02l3.25-3.5A.75.75 0 0110 3zm-3.76 9.2a.75.75 0 011.06.04l2.7 2.908 2.7-2.908a.75.75 0 111.1 1.02l-3.25 3.5a.75.75 0 01-1.1 0l-3.25-3.5a.75.75 0 01.04-1.06z"
                clipRule="evenodd"
              />
            </svg>
          </span>
        </button>
        {isOpen && (
          <ul
            className="absolute z-10 mt-1 max-h-56 w-full overflow-auto rounded-md bg-white py-1 text-base shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none sm:text-sm"
            role="listbox"
            aria-labelledby="listbox-label"
            aria-activedescendant="listbox-option-3">
            {categoryList.map((category, index) => {
              return (
                <li
                  key={index}
                  className="text-gray-900 flex cursor-default select-none justify-between py-2 pl-3 pr-3"
                  role="option"
                  onClick={() => selectOption(category)}>
                  <div className="flex items-center">
                    <span className="block truncate font-normal">{category.name}</span>
                  </div>
                  {isOptionSelected(category) && (
                    <span className="text-indigo-600 inset-y-0 right-0 flex items-center pr-4">
                      <svg className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                        <path
                          fillRule="evenodd"
                          d="M16.704 4.153a.75.75 0 01.143 1.052l-8 10.5a.75.75 0 01-1.127.075l-4.5-4.5a.75.75 0 011.06-1.06l3.894 3.893 7.48-9.817a.75.75 0 011.05-.143z"
                          clipRule="evenodd"
                        />
                      </svg>
                    </span>
                  )}
                </li>
              );
            })}
          </ul>
        )}
      </div>
    </div>
  );
}

export default MultipleSelect;

