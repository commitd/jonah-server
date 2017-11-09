import * as React from 'react'
import { Card } from 'semantic-ui-react'

type Props = {
    title?: string
    subTitle?: string
}

class SimpleCard extends React.Component<Props> {
    render() {
        const { title, subTitle, children } = this.props
        return (
            <Card fluid={true}>
                <Card.Content>
                    {title && <Card.Header>{title} {subTitle && `- ${subTitle}`}</Card.Header>}
                    {children}
                </Card.Content>
            </Card>
        )
    }
}

export default SimpleCard