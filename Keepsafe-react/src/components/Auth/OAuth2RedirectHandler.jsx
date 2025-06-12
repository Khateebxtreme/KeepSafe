import React, { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode'; //used to decode the JWT token coming from the query params.
import { useMyContext } from "../../store/ContextApi";

const OAuth2RedirectHandler = () => {
  const navigate = useNavigate();
  const location = useLocation(); //This is especially helpful when you need to read query parameters, pathname, state, or key from the current URL
  const { setToken, setIsAdmin } = useMyContext();

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const token = params.get('token');
    console.log("OAuth2RedirectHandler: Params:", params.toString());
    console.log("OAuth2RedirectHandler: Token:", token);

    if (token) {
      try {
        const decodedToken = jwtDecode(token);
        console.log("Decoded Token:", decodedToken);

        localStorage.setItem('JWT_TOKEN', token); //storing decoded token in local storage for the current session.

        const user = {
          username: decodedToken.sub,
          roles: decodedToken.roles.split(','),
        }; //retrieving user object details from decoded token. JWT subject holds the username whhen token is created and roles will have the permissions that the current user has.

        console.log("User Object:", user);
        localStorage.setItem('USER', JSON.stringify(user));

        // Update context state -> updating the global state depending on the user and his role.
        setToken(token);
        setIsAdmin(user.roles.includes('ADMIN'));

        // Delay navigation to ensure local storage operations complete before navigation (for dealing with any uncaught errors)
        setTimeout(() => {
          console.log("Navigating to /notes");
          navigate('/notes');
        }, 100); // 100ms delay
      } catch (error) {
        console.error('Token decoding failed:', error);
        navigate('/login');
      }
    } else {
      console.log("Token not found in URL, redirecting to login");
      navigate('/login');
    }
  }, [location, navigate, setToken, setIsAdmin]);

  return <div>Redirecting...</div>;
};

export default OAuth2RedirectHandler;
