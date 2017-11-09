import * as React from 'react'

import { Grid } from 'semantic-ui-react'
import Counter from './components/Counter'
import PieChart from './components/PieChart'
import BarChart from './components/BarChart'
import TimelineChart from './components/TimelineChart'
import Card from './components/Card'

type TermCount = {
    term: string,
    count: number
}

type TimeCount = {
    ts: number | Date,
    count: number
}

type OwnProps = {
    numDocuments?: number,
    numEntities?: number,
    numRelations?: number,
    numEvents?: number,
    documentTypes?: TermCount[],
    documentLanguages?: TermCount[],
    documentClassifications?: TermCount[],
    entityTypes?: TermCount[],
    documentTimeline?: TimeCount[],
}

function typeCountToXY(array: TermCount[]): { x: string, y: number }[] {
    return array.map(i => ({
        x: i.term,
        y: i.count
    }))
}

function timeCountToXY(array: TimeCount[]): { x: Date | number, y: number }[] {
    return array.map(i => ({
        x: i.ts,
        y: i.count
    }))
}

type Props = OwnProps

class View extends React.Component<Props> {

    render() {

        const { numDocuments, numEntities, numRelations,
            documentClassifications, documentLanguages,
            numEvents, documentTypes, entityTypes, documentTimeline } = this.props

        return (
            <div >
                <Grid>
                    <Grid.Row columns={4}>
                        {numDocuments != null && <Grid.Column>
                            <Counter value={numDocuments || 0} singular="document" plural="documents" />
                        </Grid.Column>}
                        {numEntities != null && <Grid.Column>
                            <Counter value={numEntities || 0} singular="entity" plural="entities" />
                        </Grid.Column>}
                        {numRelations != null && <Grid.Column>
                            <Counter value={numRelations || 0} singular="relation" plural="relations" />
                        </Grid.Column>}
                        {numEvents != null && <Grid.Column>
                            <Counter value={numEvents || 0} singular="event" plural="events" />
                        </Grid.Column>}
                    </Grid.Row>
                </Grid>
                <Grid>
                    <Grid.Row columns={1}>
                        {documentTimeline && <Grid.Column>
                            <Card title="Document timeline">
                                <TimelineChart data={timeCountToXY(documentTimeline)} />
                            </Card>
                        </Grid.Column>}
                    </Grid.Row>
                </Grid>
                <Grid><Grid.Row columns={3}>
                    {documentTypes && <Grid.Column>
                        <Card title="Types"><PieChart data={typeCountToXY(documentTypes)} /></Card>
                    </Grid.Column>}
                    {documentLanguages && <Grid.Column>
                        <Card title="Languages"><PieChart data={typeCountToXY(documentLanguages)} /></Card>
                    </Grid.Column>}
                    {documentClassifications && <Grid.Column>
                        <Card title="Classifications">
                            <PieChart data={typeCountToXY(documentClassifications)} />
                        </Card>
                    </Grid.Column>}
                </Grid.Row>
                </Grid>
                {entityTypes && <Grid><Grid.Row columns={1}><Grid.Column>
                    <Card
                        title="Entity types"
                        subTitle={`${entityTypes.length} entity types within the corpus`}
                    >
                        <BarChart data={typeCountToXY(entityTypes)} />
                    </Card>
                </Grid.Column></Grid.Row></Grid>}

            </div>
        )
    }
}

export default View