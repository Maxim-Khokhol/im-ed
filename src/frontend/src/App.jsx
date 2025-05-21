// App.js
import './App.css';
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { RegistrationPage } from './RegistrationPage';
import { ProjectPage } from './projects/ProjectPage.jsx';
import { LoginPage } from './LoginPage';
import { HomePage } from './HomePage';
import userModule from '../user.js';

export default function App() {
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            userModule.fetchUserInfo().then((user) => {
                if (user) setIsAuthenticated(true);
            }).catch(() => {
                setIsAuthenticated(false);
            });
        }
    }, []);

    const handleLogin = () => setIsAuthenticated(true);
    const handleLogout = () => {
        userModule.logout();
        setIsAuthenticated(false);
    };

    return (
        <Router>
            <div className="App">
                <Routes>
                    <Route
                        path="/"
                        element={isAuthenticated ? <HomePage /> : <Navigate to="/login" />}
                    />
                    <Route path="/login" element={<LoginPage onLogin={handleLogin} />} />
                    <Route path="/register" element={<RegistrationPage />} />
                    <Route path="/project/:projectId" element={<ProjectPage onLogout={handleLogout} />}/>
                </Routes>
            </div>
        </Router>
    );
}
