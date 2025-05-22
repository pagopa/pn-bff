const csvConfigurationMock = {
  configurationVersion: 'v2',
  configs: [
    {
      header: 'descrizione',
      field: 'description',
    },
    {
      header: 'città',
      field: 'city',
    },
    {
      header: 'via',
      field: 'address',
    },
    {
      header: 'provincia',
      field: 'province',
    },
    {
      header: 'cap',
      field: 'zipCode',
    },
    {
      header: 'telefono',
      field: 'phoneNumber',
    },
    {
      header: 'latitudine',
      field: 'latitude',
    },
    {
      header: 'longitudine',
      field: 'longitude',
    },
  ],
};

module.exports = {
  csvConfigurationMock,
};
