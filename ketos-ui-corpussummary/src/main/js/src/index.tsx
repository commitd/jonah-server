import * as React from 'react'
import * as ReactDOM from 'react-dom'
import App from './App'
import registerServiceWorker from './registerServiceWorker'
import './index.css'

import 'semantic-ui-css/semantic.min.css'

import { VesselUiPlugin } from 'vessel-plugin'
import { PluginLifecycle } from 'vessel-common'
import { Handler } from 'vessel-rpc'
import { loggerFactory } from 'vessel-utils'

const handlerLogger = loggerFactory.getLogger('Handler')

const handler: Handler<PluginLifecycle> = {
  onLoad: () => {
    handlerLogger.info('Loaded')
  },
  onUnload: () => {
    handlerLogger.info('Unloaded')
  },
  onAction: (action: string, payload?: {}) => {
    handlerLogger.info('Recieved action:' + action)
  },
  onShow: () => {
    handlerLogger.info('Shown')
  },
  onHide: () => {
    handlerLogger.info('Hide')
  },
}

ReactDOM.render(
  <VesselUiPlugin handler={handler}>
    <App />
  </VesselUiPlugin>,
  document.getElementById('root') as HTMLElement
)
registerServiceWorker()
