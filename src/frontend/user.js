// userModule.js
import axios from 'axios';

const userModule = {
    user: null,

    async fetchUserInfo() {
        try {
            const token = localStorage.getItem('token');
            console.log('Токен:', token);

            if (!token) {
                console.error('Token not found. The user is not authenticated.');
                return null;
            }

            const response = await axios.get(`/api/user`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });


            this.user = response.data;
            return this.user;
        } catch (error) {
            console.error('Error getting user information:', error);
            throw error;
        }
    },

    logout(navigate) {
        localStorage.removeItem('token');

        this.user = null;
        console.log('The token has been deleted. The user has logged out.');
        navigate('/login');
    },
};

export default userModule;
