type Props = {
  text: string;
  className?: string;
};

function Tag({ text, className }: Props) {
  return <div className={`
  bg-primary-100
  text-sm
  px-2 py-2 mr-4
  rounded-md flex justify-center items-center '
  ${className}`}>{text}</div>;
}

export default Tag;
