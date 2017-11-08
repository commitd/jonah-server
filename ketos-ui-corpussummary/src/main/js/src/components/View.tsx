import * as React from 'react'

import Grid from 'material-ui/Grid'
import { withStyles, StyleRulesCallback, WithStyles, Theme } from 'material-ui/styles'
import Counter from './Counter'

const styles: StyleRulesCallback = (theme: Theme) => ({
    root: {
        flexGrow: 1,
        margin: 10,
    }
})

type OwnProps = {
    numDocuments?: number,
    numEntities?: number,
    numRelations?: number,
    numEvents?: number
}

type Props = WithStyles & OwnProps

class View extends React.Component<Props> {

    static defaultProps: OwnProps = {
        numDocuments: 0,
        numEntities: 0,
        numRelations: 0,
        numEvents: 0
    }

    render() {
        const { classes } = this.props

        const { numDocuments, numEntities, numRelations, numEvents } = this.props

        return (
            <div className={classes.root} >
                <Grid container={true}>
                    <Grid item={true} xs={3}>
                        <Counter value={numDocuments || 0} singular="document" plural="documents" />
                    </Grid>
                    <Grid item={true} xs={3}>
                        <Counter value={numEntities || 0} singular="entity" plural="entities" />
                    </Grid>
                    <Grid item={true} xs={3}>
                        <Counter value={numRelations || 0} singular="relation" plural="relations" />
                    </Grid>
                    <Grid item={true} xs={3}>
                        <Counter value={numEvents || 0} singular="event" plural="events" />
                    </Grid>
                </Grid>
            </div>
        )
    }
}

export default withStyles(styles)(View)