import * as React from 'react'
import { ChildProps } from 'vessel-plugin'
import { graphql, gql, QueryProps } from 'react-apollo'

import View from './ViewContainer'
import DatasetSelector from './containers/DatasetSelectorContainer'
import { Container } from 'semantic-ui-react'
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
      <Container fluid={false}>
        <DatasetSelector selectedDataset={datasetId} onDatasetSelected={this.handleDatasetSelected} />
        {datasetId && <View dataset={datasetId} />}
      </Container>
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
