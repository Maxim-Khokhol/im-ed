// RegistrationPage.js
import React, { useState } from "react";
import axios from "axios";
import {useNavigate} from "react-router-dom";

export function RegistrationPage() {
    const navigate = useNavigate();
    const [user, setUser] = useState({
        username: "",
        password: "",
        email: ""
    });

    function handleChange(event) {
        const { name, value } = event.target;
        setUser(prevUser => ({
            ...prevUser,
            [name]: value
        }));
    }

    function resetForm() {
        setUser({
            username: "",
            password: "",
            email: ""
        });
    }

    function registerUser() {
        axios.post('http://localhost:9000/api/registration', user)
            .then(response => {
                console.log(response.data);
                resetForm();
            })
            .catch(error => {
                console.error(error);
            });
    }

    return (
        <div className="RegistrationPage">
            <h2>Registration</h2>
            <form onSubmit={(e) => { e.preventDefault(); registerUser(); }}>
                <div>
                    <label>Username:</label>
                    <input
                        type="text"
                        name="username"
                        value={user.username}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div>
                    <label>Password:</label>
                    <input
                        type="password"
                        name="password"
                        value={user.password}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div>
                    <label>Email:</label>
                    <input
                        type="email"
                        name="email"
                        value={user.email}
                        onChange={handleChange}
                        required
                    />
                </div>
                <button type="submit">Sign up</button>
            </form>
            <button onClick={() => navigate("/login")}>Login</button>
        </div>
    );
}
