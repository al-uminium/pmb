/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      fontFamily: {
        'oswald': ["Oswald"],
        'roboto': ["Roboto"]
      }
    },
  },
  plugins: [],
}