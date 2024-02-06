import { FC, useEffect, useRef, useState } from 'react';

export type SelectOption = {
  label: string;
  value: string | number;
};

type SingleSelectProps = {
  multiple?: false;
  value?: SelectOption;
  onChange: (value?: SelectOption) => void;
};
type MultipleSelectProps = {
  multiple: true;
  value: SelectOption[];
  onChange: (value: SelectOption[]) => void;
};
type Props = {
  options: SelectOption[];
} & (SingleSelectProps | MultipleSelectProps);

const Select: FC<Props> = ({ multiple, value, onChange, options }) => {
  const [isOpen, setIsOpen] = useState(false);
  const [highlightedIndex, setHighlightedIndex] = useState(0);
  const containerRef = useRef<HTMLDivElement>(null);

  function clearOptions() {
    multiple ? onChange([]) : onChange(undefined);
  }
  function isOptionSelected(option: SelectOption) {
    return multiple ? value.includes(option) : option === value;
  }
  function selectOption(option: SelectOption) {
    if (multiple) {
      isOptionSelected(option) ? onChange(value.filter((o) => o !== option)) : onChange([...value, option]);
    } else {
      if (!isOptionSelected(option)) onChange(option);
    }
  }

  useEffect(() => {
    if (isOpen) setHighlightedIndex(0);
    return () => setHighlightedIndex(0);
  }, [isOpen]);

  useEffect(() => {
    const handler = (e: KeyboardEvent) => {
      if (e.target !== containerRef.current) return;
      switch (e.code) {
        case 'Enter':
        case 'Space':
          setIsOpen((prev) => !prev);
          if (isOpen) selectOption(options[highlightedIndex]);
          break;
        case 'ArrowUp':
        case 'ArrowDown':
          if (!isOpen) {
            setIsOpen(true);
            break;
          } else {
            let newIndex = highlightedIndex + (e.code === 'ArrowDown' ? 1 : -1);
            newIndex = newIndex < 0 ? options.length - 1 : newIndex >= options.length ? 0 : newIndex;
            setHighlightedIndex(newIndex);
            break;
          }
        case 'Escape':
          setIsOpen(false);
          break;
      }
    };
    containerRef.current?.addEventListener('keydown', handler);

    return () => containerRef.current?.removeEventListener('keydown', handler);
  }, [isOpen, highlightedIndex, options]);

  return (
    <div
      ref={containerRef}
      className="relative flex h-[45px] items-center gap-2 rounded-xl bg-background-100 p-2 focus:border-sky-500"
      tabIndex={0}
      onClick={() => setIsOpen((prev) => !prev)}
      onBlur={() => setIsOpen(false)}>
      <span className="flex grow flex-wrap gap-2">
        {multiple
          ? value.map((v) => (
              <button
                key={v.value}
                className="focus::bg-background-200 focus::border-red-500 group flex items-center gap-1 rounded border border-slate-500 px-1 text-sm hover:border-red-500 hover:bg-red-200"
                onClick={(e) => {
                  e.stopPropagation();
                  selectOption(v);
                }}>
                {v.label}
                <span className="text-lg text-slate-500">&times;</span>
              </button>
            ))
          : value?.label}
      </span>
      <button
        className="text-lg text-slate-500 hover:text-red-500 focus:text-red-500"
        onClick={(e) => {
          e.stopPropagation();
          clearOptions();
        }}>
        &times;
      </button>
      <div className="w-[1px] self-stretch bg-background-400"></div>
      <div className="translate-y-1/4 border-[0.25rem] border-transparent border-t-background-400"></div>
      <ul className={`absolute left-0 top-full z-10 max-h-60 w-full overflow-y-auto rounded-xl border bg-background-100 ${isOpen ? 'block' : 'hidden'}`}>
        {options.map((option, index) => (
          <li
            key={option.value}
            className={`cursor-pointer py-1 px-2  ${index === highlightedIndex ? 'bg-sky-500 text-black' : isOptionSelected(option) ? 'bg-primary-100' : ''}`}
            onClick={(e) => {
              e.stopPropagation();
              selectOption(option);
              setIsOpen(false);
            }}
            onMouseEnter={() => setHighlightedIndex(index)}>
            {option.label}
          </li>
        ))}
      </ul>
    </div>
  );
};
export default Select;

