import * as React from 'react'

import { storiesOf } from '@storybook/react'

import View from '../src/View'

const documentTypes = [
    {
        type: 'word',
        count: 233
    },
    {
        type: 'excel',
        count: 78
    },
    {
        type: 'powerpoint',
        count: 33
    },
]

const entityTypes = [
    {
        type: 'people',
        count: 233
    },
    {
        type: 'location',
        count: 78
    },
    {
        type: 'organisation',
        count: 33
    },
    {
        type: 'temporal',
        count: 100
    }
]

const documentTimeline = [
    { ts: new Date(1982, 1, 1), count: 125 },
    { ts: new Date(1987, 1, 1), count: 257 },
    { ts: new Date(1993, 1, 1), count: 345 },
    { ts: new Date(1997, 1, 1), count: 515 },
    { ts: new Date(2001, 1, 1), count: 132 },
    { ts: new Date(2005, 1, 1), count: 305 },
    { ts: new Date(2011, 1, 1), count: 270 },
    { ts: new Date(2015, 1, 1), count: 470 }
]

storiesOf('View', module)
    .add('No props', () => <View />)
    .add('All props', () => (
        <View
            numDocuments={100000}
            numEntities={2021340}
            numEvents={42}
            numRelations={12331}
            documentTypes={documentTypes}
            entityTypes={entityTypes}
            documentTimeline={documentTimeline}
        />))
