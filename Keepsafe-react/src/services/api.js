import axios from "axios";

console.log("API URL:", import.meta.env.VITE_REACT_APP_API_URL);

// Create an Axios instance -> Setting up the base URL, default headers etc that would be used across all our api requests. It is a centralized config for all our requests.
const api = axios.create({
  baseURL: `${import.meta.env.VITE_REACT_APP_API_URL}/api`,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
  withCredentials: true, 
});
//withCredentials property is a boolean value that indicates whether or not cross-site Access-Control requests should be made using credentials such as cookies, authentication headers or TLS client certificates. Setting withCredentials has no effect on same-origin requests.

// Add a request interceptor to include JWT. -> Interceptors are used to modify our requests before it is sent to the server.
api.interceptors.request.use(
  async (config) => {
    //for each request, we are using the JWT token that is stored in our local storage before processing it.
    const token = localStorage.getItem("JWT_TOKEN");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`; //format in which we are sending this header into the server.
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default api; //we will be making use of this api to make and configure all our requests to the server.