import * as React from 'react'

import { storiesOf } from '@storybook/react'

import View from '../src/components/View'

storiesOf('View', module)
    .add('No props', () => <View />)
    .add('All props', () => <View numDocuments={100000} numEntities={2000000} numEvents={40} numRelations={12300} />)
