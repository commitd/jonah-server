import * as React from 'react'
import { ChildProps } from 'vessel-plugin'
type Props = ChildProps

class App extends React.Component<Props> {
  render() {
    return (
      <div>
        <div>
          <h2>Welcome to Vessel</h2>
        </div>
        <p>
          To get started, edit <code>src/App.tsx</code> and save to reload.
        </p>
      </div>
    )
  }
}

export default App
