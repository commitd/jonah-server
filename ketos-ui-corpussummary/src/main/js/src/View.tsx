import * as React from 'react'

import Grid from 'material-ui/Grid'
import { withStyles, StyleRulesCallback, WithStyles, Theme } from 'material-ui/styles'
import Counter from './components/Counter'
import PieChart from './components/PieChart'
import TimelineChart from './components/TimelineChart'
import Paper from 'material-ui/Paper'

const styles: StyleRulesCallback = (theme: Theme) => ({
    root: {
        flexGrow: 1,
        margin: 10,
    },
    graph: {
        maxHeight: 400,
        height: 400
    }
})

type TypeCount = {
    type: string,
    count: number
}

type TimeCount = {
    ts: Date,
    count: number
}

type OwnProps = {
    numDocuments?: number,
    numEntities?: number,
    numRelations?: number,
    numEvents?: number,
    documentTypes?: TypeCount[],
    entityTypes?: TypeCount[],
    documentTimeline?: TimeCount[],
}

function typeCountToXY(array: TypeCount[]): { x: string, y: number }[] {
    return array.map(i => ({
        x: i.type,
        y: i.count
    }))
}

function timeCountToXY(array: TimeCount[]): { x: Date, y: number }[] {
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
                <Grid container={true}>
                    {documentTypes && <Grid item={true} xs={6}>
                        <Paper><PieChart data={typeCountToXY(documentTypes)} /></Paper>
                    </Grid>}
                    {entityTypes && <Grid item={true} xs={6}>
                        <Paper><PieChart data={typeCountToXY(entityTypes)} /></Paper>
                    </Grid>}
                </Grid>
                <Grid container={true} className={classes.graph}>
                    {documentTimeline && <Grid item={true} xs={12}>
                        <Paper><TimelineChart data={timeCountToXY(documentTimeline)} /></Paper>
                    </Grid>}
                </Grid>
            </div>
        )
    }
}

export default withStyles(styles)(View)