import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "./App.css";
import userModule from "../user.js";

export function HomePage() {
    const [projects, setProjects] = useState([]);
    const [newProjectName, setNewProjectName] = useState("");
    const [showModal, setShowModal] = useState(false);
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        async function initialize() {
            try {
                const userData = await userModule.fetchUserInfo();
                if (userData) {
                    setUser(userData);

                    const response = await axios.get(`/api/projects`, {
                        params: { userId: userData.id },
                    });
                    setProjects(response.data);
                }
            } catch (error) {
                console.error("Error initializing user or fetching projects:", error);
            } finally {
                setLoading(false);
            }
        }

        initialize();
    }, []);



    const cloneProject = async (projectId) => {
        try {
            const response = await axios.post(`/api/projects/${projectId}/clone`);
            const clonedProject = response.data;
            setProjects((prev) => [...prev, clonedProject]);
        } catch (error) {
            console.error("Error cloning project:", error);
        }
    };

    const createProject = async () => {
        try {
            const response = await axios.post("/api/projects/createProject", {
                name: newProjectName,
                userId: user.id,
            });
            const newProject = response.data;
            setProjects((prevProjects) => [...prevProjects, newProject]);
            setShowModal(false);
        } catch (error) {
            console.error("Error creating project:", error);
        }
    };

    const handleCreateButtonClick = () => setShowModal(true);

    const handleModalSubmit = (e) => {
        e.preventDefault();
        if (newProjectName.trim()) {
            createProject();
        }
    };

    if (loading) {
        return <div>Loading...</div>;
    }

    return (
        <div>
            <h1>Your Projects</h1>
            <button onClick={handleCreateButtonClick}>Create a new project</button>

            {showModal && (
                <div className="modal">
                    <div className="modal-content">
                        <h2>Create a new project</h2>
                        <form onSubmit={handleModalSubmit}>
                            <label>
                                Project Name:
                                <input
                                    type="text"
                                    value={newProjectName}
                                    onChange={(e) => setNewProjectName(e.target.value)}
                                    required
                                />
                            </label>
                            <button type="submit">Create</button>
                            <button type="button" onClick={() => setShowModal(false)}>
                                Cancel
                            </button>
                        </form>
                    </div>
                </div>
            )}

            <div>
                {projects.length > 0 ? (
                    <ul>
                        {projects.map((project) => (
                            <li
                                key={project.id}
                                onDoubleClick={() => navigate(`/project/${project.id}`)}
                                className="project-item"
                            >
                                {project.name || `Project #${project.id}`}
                                <button onClick={() => cloneProject(project.id)}>Clone Project</button>
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p>No projects available.</p>
                )}
            </div>
        </div>
    );
}


