import * as React from 'react'

import { storiesOf } from '@storybook/react'

import App from '../src/App'

import './Counter.tsx'

storiesOf('App', module)
    .add('renders', () => <App />)
