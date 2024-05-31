function daysBetween(firstDate, secondDate) {
  const differenceInMilliseconds = secondDate - firstDate;
  const millisecondsPerDay = 86400000;
  return differenceInMilliseconds / millisecondsPerDay;
}

function checkIfIntervalPassed(latestFile) {
  const generateInterval = parseInt(process.env.GENERATE_INTERVAL, 10);
  console.log('Checking interval, generateInterval:', generateInterval);

  if (isNaN(generateInterval) || generateInterval < 0) {
    throw new Error('Invalid or missing GENERATE_INTERVAL environment variable');
  }

  const lastModified = new Date(latestFile.LastModified);
  const now = new Date();
  const daysDifference = daysBetween(lastModified, now);
  console.log('Days since last modification:', daysDifference);

  return daysDifference > generateInterval;
}

module.exports = { checkIfIntervalPassed };