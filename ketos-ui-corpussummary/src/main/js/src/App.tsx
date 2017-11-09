import * as React from 'react'
import { ChildProps } from 'vessel-plugin'
import { graphql, gql, QueryProps } from 'react-apollo'

import View from './View'
import DatasetSelector from './containers/DatasetSelectorContainer'

interface Response {
  corpora: {
    id: string
    name: string
  }[]
}

interface GqlProps {
  data?: QueryProps & Partial<Response>
}

type OwnProps = {}

type Props = OwnProps & GqlProps & ChildProps

type State = {
  datasetId?: string,
}

class App extends React.Component<Props, State> {

  state: State = {

  }

  handleDatasetSelected = (datasetId: string) => {
    this.setState({
      datasetId
    })
  }

  render() {

    const { datasetId } = this.state

    return (
      <div>
        <DatasetSelector selectedDataset={datasetId} onDatasetSelected={this.handleDatasetSelected} />
        {datasetId && <View />}
      </div>
    )
  }
}

const CORPUS_SUMMARY_QUERY = gql`
query Corpora {
  corpora {
    id
    name
  }
}
`

export default graphql<Response, OwnProps & ChildProps, Props>(CORPUS_SUMMARY_QUERY)(App)
