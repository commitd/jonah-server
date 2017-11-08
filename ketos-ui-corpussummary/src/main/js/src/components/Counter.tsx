import * as React from 'react'

import Paper from 'material-ui/Paper'
import { withStyles, StyleRulesCallback, WithStyles, Theme } from 'material-ui/styles'
import Typography from 'material-ui/Typography'

import * as numeral from 'numeral'

const counterStyle: StyleRulesCallback = (theme: Theme) => ({
    paper: {
        padding: 16,
        textAlign: 'center',
        color: theme.palette.text.secondary,
    },
})

type CounterProps = {
    value: number,
    singular: string,
    plural: string
}

class Counter extends React.Component<CounterProps & WithStyles> {

    render() {
        const { classes, value, singular, plural } = this.props

        const title = value === 1 ? singular : plural
        return (
            <Paper className={classes.paper}>
                <Typography type="display2">{numeral(value).format('0,0')}</Typography>
                <Typography type="body2">{title}</Typography>
            </Paper>
        )
    }
}

export default withStyles(counterStyle)(Counter)