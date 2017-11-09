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
        />))
