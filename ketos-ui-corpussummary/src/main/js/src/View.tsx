import * as React from 'react'

import Grid from 'material-ui/Grid'
import { withStyles, StyleRulesCallback, WithStyles, Theme } from 'material-ui/styles'
import Counter from './components/Counter'
import PieChart from './components/PieChart'
import BarChart from './components/BarChart'
import TimelineChart from './components/TimelineChart'
import Card from './components/Card'

const styles: StyleRulesCallback = (theme: Theme) => ({
    root: {
        flexGrow: 1,
        margin: 10,
    },
    graph: {

    }
})

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

type Props = WithStyles & OwnProps

class View extends React.Component<Props> {

    render() {
        const { classes } = this.props

        const { numDocuments, numEntities, numRelations,
            documentClassifications, documentLanguages,
            numEvents, documentTypes, entityTypes, documentTimeline } = this.props

        return (
            <div className={classes.root} >
                <Grid container={true}>
                    {numDocuments != null && <Grid item={true} xs={3}>
                        <Counter value={numDocuments || 0} singular="document" plural="documents" />
                    </Grid>}
                    {numEntities != null && <Grid item={true} xs={3}>
                        <Counter value={numEntities || 0} singular="entity" plural="entities" />
                    </Grid>}
                    {numRelations != null && <Grid item={true} xs={3}>
                        <Counter value={numRelations || 0} singular="relation" plural="relations" />
                    </Grid>}
                    {numEvents != null && <Grid item={true} xs={3}>
                        <Counter value={numEvents || 0} singular="event" plural="events" />
                    </Grid>}
                </Grid>
                <Grid container={true} className={classes.graph}>
                    {documentTimeline && <Grid item={true} xs={12}>
                        <Card title="Document timeline"><TimelineChart data={timeCountToXY(documentTimeline)} /></Card>
                    </Grid>}
                </Grid>
                <Grid container={true}>
                    {documentTypes && <Grid item={true} xs={4}>
                        <Card title="Types"><PieChart data={typeCountToXY(documentTypes)} /></Card>
                    </Grid>}
                    {documentLanguages && <Grid item={true} xs={4}>
                        <Card title="Languages"><PieChart data={typeCountToXY(documentLanguages)} /></Card>
                    </Grid>}
                    {documentClassifications && <Grid item={true} xs={4}>
                        <Card title="Classifications">
                            <PieChart data={typeCountToXY(documentClassifications)} />
                        </Card>
                    </Grid>}
                </Grid>
                <Grid container={true}>
                    {entityTypes && <Grid item={true} xs={12}>
                        <Card
                            title="Entity types"
                            subTitle={`${entityTypes.length} entity types within the corpus`}
                        >
                            <BarChart data={typeCountToXY(entityTypes)} />
                        </Card>
                    </Grid>}
                </Grid>

            </div>
        )
    }
}

export default withStyles(styles)(View)