import React, { createContext, useContext, useState } from "react";
import { useEffect } from "react";
import api from "../services/api";
import toast from "react-hot-toast";

const ContextApi = createContext();

export const ContextProvider = ({ children }) => {
  //find the JWT token in the localstorage that was stored when user logged in.
  const getToken = localStorage.getItem("JWT_TOKEN")
    ? JSON.stringify(localStorage.getItem("JWT_TOKEN"))
    : null;

  //find is the user status from the localstorage i.e checking if the user is an admin or not.
  const isADmin = localStorage.getItem("IS_ADMIN")
    ? JSON.stringify(localStorage.getItem("IS_ADMIN"))
    : false;

  //store the token in state so it can be used across the application.
  const [token, setToken] = useState(getToken);

  //store the current loggedin user
  const [currentUser, setCurrentUser] = useState(null);
  //handle sidebar opening and closing in the admin panel
  const [openSidebar, setOpenSidebar] = useState(true);
  //check the loggedin user is admin or not
  const [isAdmin, setIsAdmin] = useState(isADmin);

  const fetchUser = async () => {
    //simply fetches the current user data from the api when the user is logged in.
    const user = JSON.parse(localStorage.getItem("USER"));

    if (user?.username) {
      //cehcking for the existence of logged in user.
      try {
        const { data } = await api.get(`/auth/user`); //allows us to get all user data information from the backend when we query this endpoint as a logged in user.

        const roles = data.roles; //getting user roles from the response of the above api request.

        if (roles.includes("ROLE_ADMIN")) {
          localStorage.setItem("IS_ADMIN", JSON.stringify(true));
          setIsAdmin(true); //if the role is admin at the backend, we are updating the local storage so It can be easily tracked across the application on front end side of things.
        } else {
          localStorage.removeItem("IS_ADMIN"); //if user is not admin, remove isAdmin key from local storage so it can be treated like a normal user.
          setIsAdmin(false);
        }
        setCurrentUser(data); //updating the current user data with help of response from backend. This will be now used across the application to see the roles, authorities that the current logged in user has.
      } catch (error) {
        console.error("Error fetching current user", error);
        toast.error("Error fetching current user");
      }
    }
  };

  //if  token exist fetch the current user
  useEffect(() => {
    if (token) {
      fetchUser();
    }
  }, [token]);

  //through context provider you are sending all the datas so that we access at anywhere in your application
  return (
    <ContextApi.Provider
      value={{
        token,
        setToken,
        currentUser,
        setCurrentUser,
        openSidebar,
        setOpenSidebar,
        isAdmin,
        setIsAdmin,
      }}
    >
      {children}
    </ContextApi.Provider>
  );
};

//by using this (useMyContext) custom hook we can reach our context provier and access the datas across our components
export const useMyContext = () => {
  const context = useContext(ContextApi);

  return context;
};