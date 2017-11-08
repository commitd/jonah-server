import * as React from 'react'

import { storiesOf } from '@storybook/react'

import View from '../src/components/View'

storiesOf('View', module)
    .add('No props', () => <View />)
    .add('All props', () => <View numDocuments={0} numEntities={0} numEvents={0} numRelations={0} />)
