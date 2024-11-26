/**
 * Check if the last data date is older than the alarm days
 * @param index - Index object
 * @param alarmNDays - Number of alarm days
 */
const checkLastDataDate = (index, alarmNDays) => {
  const referenceDate = new Date();
  const lastDate = new Date(index.lastDate);

  referenceDate.setDate(referenceDate.getDate() - alarmNDays);

  if (lastDate < referenceDate) {
    console.error(
      `No data in the last ${alarmNDays} days. Last data date: ${index.lastDate}`
    );
  }
};

module.exports = { checkLastDataDate };
