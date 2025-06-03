const raddAltApiResponse = [
  {
    description: 'CAF LAVORO E FISCO SRL',
    address: {
      city: 'ABANO TERME',
      addressRow: 'VIA GIACOMO MATTEOTTI 37',
      pr: 'PD',
      cap: '35031',
    },
    phoneNumber: '3483419899',
  },
  {
    description: 'CAF ITALIA',
    address: {
      city: 'ACATE',
      addressRow: 'VIA CARDUCCI 84',
      pr: 'RG',
      cap: '97011',
    },
    phoneNumber: '0932656649',
  },
  {
    description: 'CAF SILPA',
    address: {
      city: 'ACIREALE',
      addressRow: 'VIA DAFNICA 163',
      pr: 'CT',
      cap: '95024',
    },
    phoneNumber: '34845033111',
  },
  {
    description: 'CAF UCI SRL',
    address: {
      city: 'PARMA',
      addressRow: 'STRADA GIOVANNI INZANI 29',
      pr: 'PR',
      cap: '43125',
    },
    phoneNumber: '0521030444',
  },
  {
    description: 'CAF ACLI',
    address: {
      city: 'BERGAMO',
      addressRow: 'VIA EVARISTO BASCHENIS 100',
      pr: 'BG',
      cap: '24122',
    },
    phoneNumber: '03500648889',
  },
];

const mockGeocodeResponse = [
  {
    ResultItems: [
      {
        Title: 'VIA GIACOMO MATTEOTTI 37, ABANO TERME (PD) - 35031',
        Position: [9.1876, 45.4669],
        MatchScores: {
          Overall: 0.9,
        },
      },
    ],
  },
  {
    ResultItems: [
      {
        Title: 'VIA CARDUCCI 84, ACATE (RG) - 97011',
        Position: [10.5423, 45.9076],
        MatchScores: {
          Overall: 0.85,
        },
      },
    ],
  },
  {
    ResultItems: [
      {
        Title: 'VIA DAFNICA 163, ACIREALE (CT) - 95024',
        Position: [11.2322, 46.9765],
        MatchScores: {
          Overall: 0.7,
        },
      },
    ],
  },
  {
    ResultItems: [
      {
        Title: 'STRADA GIOVANNI INZANI 29, PARMA (PR) - 43125',
        Position: [9.121, 45.6555],
        MatchScores: {
          Overall: 0.8,
        },
      },
    ],
  },
  {
    ResultItems: [
      {
        Title: 'VIA EVARISTO BASCHENIS 100, BERGAMO (BG) - 24122',
        Position: [9.9098, 45.129],
        MatchScores: {
          Overall: 0.76,
        },
      },
    ],
  },
];

module.exports = {
  raddAltApiResponse,
  mockGeocodeResponse,
};
