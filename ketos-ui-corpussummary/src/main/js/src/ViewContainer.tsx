import * as React from 'react'
import { graphql, gql, QueryProps } from 'react-apollo'
import View from './View'

interface OwnProps {
    dataset: string
}

interface Response {
    corpus: {
        id: string
        name: string
        documentCount: number
        entityCount: number
        relationCount: number
        documentTypes: {
            bins: {
                count: number
                term: string
            }[]
        }
        documentLanguages: {
            bins: {
                count: number
                term: string
            }[]
        }
        documentClassifications: {
            bins: {
                count: number
                term: string
            }[]
        }
        entityTypes: {
            bins: {
                count: number
                term: string
            }[]
        }
        documentTimeline: {
            bins: {
                ts: string
                count: number
            }[]
        }
    }
}

interface GqlProps {
    data?: QueryProps & Partial<Response>
}

type Props = OwnProps & GqlProps

const container = (props: Props) => {
    const { data } = props

    if (!data || data.loading || !data.corpus) {
        return <div />
    }

    return (
        <View
            numDocuments={data.corpus.documentCount}
            numEntities={data.corpus.entityCount}
            numRelations={data.corpus.relationCount}
            documentTypes={data.corpus.documentTypes.bins}
            documentLanguages={data.corpus.documentLanguages.bins}
            documentClassifications={data.corpus.documentClassifications.bins}
            entityTypes={data.corpus.entityTypes.bins}
            documentTimeline={data.corpus.documentTimeline.bins.map(b => ({ ts: Date.parse(b.ts), count: b.count }))}
        />
    )
}

const CORPUS_INFO_QUERY = gql`
query CorpusInfo($datasetId: String!)  {
  corpus(id:$datasetId) {
    id
    name
    documentCount
    entityCount
    relationCount
    documentTypes {
        bins {
            count
            term
        }
    }
    documentLanguages {
        bins {
            count
            term
        }
    }
    documentClassifications {
        bins {
            count
            term
        }
    }
    entityTypes {
        bins {
            count
            term
        }
    }
    documentTimeline {
        bins {
            ts
            count
        }
    }
  }
}
`

export default graphql<Response, OwnProps, Props>(CORPUS_INFO_QUERY, {
    options: ({ dataset }: OwnProps) => ({ variables: { datasetId: dataset } }),
})(container)
