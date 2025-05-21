import React, { useEffect, useState } from 'react';
import axios from 'axios';
import userModule from '../user.js';
import { useNavigate } from 'react-router-dom';

export function LoginPage({ onLogin }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null);
    const navigate = useNavigate();


    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:9000/api/auth', { username, password });
            const { token } = response.data;


            localStorage.setItem('token', token);
            await userModule.fetchUserInfo();
            onLogin();


            navigate('/');
        } catch (err) {
            setError('Login failed. Please check your credentials.');
            console.error('Error during login:', err);
        }
    };


    return (
        <div>
            <h2>Login</h2>
            <form onSubmit={handleLogin}>
                <div>
                    <label>Username:</label>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Password:</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">Login</button>
                <button onClick={() => navigate("/register")}>Registration</button>
            </form>
            {error && <p style={{ color: 'red' }}>{error}</p>}
        </div>
    );
}

