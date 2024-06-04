const axios = require('axios');
const raddStoreRegistryApiUrl = process.env.RADD_STORE_REGISTRY_API_URL;

async function fetchApi(lastKey, limit) {
  const params = {
    limit: limit || 1000,
    lastKey: lastKey || null
  };

  console.log('Fetching API data with params:', params);

  try {
    const response = await axios.get(`${raddStoreRegistryApiUrl}`, { params });
    console.log('API response received');
    return response.data;
  } catch (error) {
    console.error('Error fetching data from API:', error);
    throw new Error('Failed to fetch data from API');
  }
}

module.exports = { fetchApi };