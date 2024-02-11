/** @type {import('tailwindcss').Config} */

import colors from 'tailwindcss/colors';

export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    colors: {
      primary: {
        DEFAULT: colors.indigo[500],
        light: colors.indigo[50],
        ...colors.indigo,
      },
      danger: {
        DEFAULT: colors.red[400],
        ...colors.red,
      },
      background: colors.zinc,

      white: colors.white,
      black: colors.black,
      transparent: colors.transparent,
    },
  },
  plugins: [],
};

