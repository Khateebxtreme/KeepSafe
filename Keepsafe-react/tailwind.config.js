const { Gradient } = require('@mui/icons-material')

/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      backgroundImage: {
        'theme-gradient': 'linear-gradient(to right, #3A4050, #4A5A80, #4A3A70)',
      },
      colors: {
        headerColor: "#1C2230",
        textColor: "#ffffff",
        btnColor: "#5A67D8",
        btnColorHover:"#6B7DE2",
        noteColor: "#F4BDAB",
      },
      fontWeight: {
        customWeight: 500,
      },
      height: {
        headerHeight: "74px",
      },
      maxHeight: {
        navbarHeight: "420px",
      },
      minHeight: {
        customHeight: "530px",
      },
      fontFamily: {
        montserrat: ["Montserrat"],
        dancingScript: ["Dancing Script"],
        anton : ["Anton"]
      },
      fontSize: {
        logoText: "35px",
        customText: "15px",
        tablehHeaderText: "16px",
        headerText: ["50px", "60px"],
        tableHeader: ["15px", "25px"],
      },

      backgroundColor: {
        customRed: "rgba(172, 30, 35, 1)",
        testimonialCard: "#F9F9F9",
      },
      boxShadow: {
        custom: "0 0 15px rgba(0, 0, 0, 0.3)",
      },
    },
  },
    variants: {
    extend: {
      backgroundImage: ["responsive"],
    },
  },
  plugins: [],
}

