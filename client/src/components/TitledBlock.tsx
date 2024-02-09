import { PropsWithChildren } from 'react';

type Props = {
  title: string;
  className?: string;
};
function TitledBlock({ title, children, className = '' }: PropsWithChildren<Props>) {
  return (
    <div className={className}>
      <h2 className="mb-2 text-xl font-semibold">{title}</h2>
      {children}
    </div>
  );
}

export default TitledBlock;

