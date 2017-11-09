import * as React from 'react'
import Card, { CardHeader, CardContent } from 'material-ui/Card'

type Props = {
    title?: string
    subTitle?: string
}

class SimpleCard extends React.Component<Props> {
    render() {
        const { title, subTitle, children } = this.props
        return (
            <Card>
                {(title || subTitle) && <CardHeader title={title} subheader={subTitle} />}
                <CardContent>
                    {children}
                </CardContent>
            </Card>
        )
    }
}

export default SimpleCard