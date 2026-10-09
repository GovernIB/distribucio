import { FormApi, FormField, MuiForm, useBaseAppContext, useMuiContentDialog, useResourceApiService } from 'reactlib';
import DOMPurify from 'dompurify';
import React, { useRef } from 'react';
import { Button, Grid, Typography, Icon } from '@mui/material';
import { formatDate } from '../util/dateUtils';
import { useDistribucioContext } from '../components/DistribucioContext.ts';

const commentProps = {
    padding: '8px 16px',
    borderRadius: '8px',
};

const myCommentProps = {
    ...commentProps,
    alignSelf: 'end',
    color: 'success.contrastText',
    backgroundColor: 'success.main',
};

const otherCommentProps = {
    ...commentProps,
    backgroundColor: 'greyBackground',
};

// eslint-disable-next-line react-refresh/only-export-components
const Comments = (props: any) => {
    const { t } = useBaseAppContext();
    const { resourceName, id, resourceReference, readOnly, i18nKeys } = props;
    const { currentUser } = useDistribucioContext();
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService(resourceName);
    const [comments, setComments] = React.useState<any[]>();
    const [formKey, setFormKey] = React.useState(0);
    const { temporalMessageShow } = useBaseAppContext();
    const formApiRef = React.useRef<FormApi | any>({});
    const ref = useRef<HTMLDivElement | null>(null);
    const refresh = () => {
        apiFind({
            filter: `${resourceReference}.id:${id}`,
            includeLinksInRows: true,
            unpaged: true,
            sorts: ['createdDate,asc'],
        })
            .then((result) => {
                setComments(result.rows);
                setTimeout(() => {
                    const contentRef = ref.current?.parentElement;
                    if (contentRef) {
                        contentRef.scrollTop = contentRef.scrollHeight;
                    }
                }, 100);
            })
            .catch((error) => {
                if (error?.message) temporalMessageShow(null, error?.message, 'error');
            });
    };

    const handleButtonClick = () => {
        formApiRef.current?.save().then(() => {
            refresh();
            setFormKey((key) => key + 1);
        });
    };

    React.useEffect(() => {
        if (apiIsReady) {
            refresh();
        }
    }, [apiIsReady]);

    return (
        <Grid
            container
            direction="column"
            rowGap={1}
            component={'div'}
            ref={ref}
            sx={{ justifyContent: 'center', alignItems: 'flex-start', pb: 1 }}
        >
            {comments?.map((comment: any) => {
                const myComment = comment?.createdBy == currentUser.id;
                return (
                    <Grid key={comment?.id} sx={myComment ? myCommentProps : otherCommentProps}>
                        <Typography variant="subtitle2" color={myComment ? 'success.contrastText' : 'text.primary'}>
                            {comment?.createdBy}
                        </Typography>
                        <Typography
                            variant="body2"
                            dangerouslySetInnerHTML={{ __html: DOMPurify.sanitize(comment?.text) }}
                        />
                        <Typography variant="caption" color={myComment ? 'success.contrastText' : 'text.secondary'}>
                            {formatDate(comment?.createdDate)}
                        </Typography>
                    </Grid>
                );
            })}

            {!readOnly && (
                <Grid size={12}>
                    <MuiForm
                        key={formKey}
                        resourceName={resourceName}
                        hiddenToolbar
                        additionalData={{
                            [resourceReference]: { id },
                        }}
                        apiRef={formApiRef}
                        commonFieldComponentProps={{ size: 'small' }}
                        i18nKeys={i18nKeys}
                    >
                        <Grid container columnSpacing={1} rowSpacing={1}>
                            <Grid size={12} sx={{ display: 'flex' }}>
                                <FormField name="text" />
                                <Button onClick={handleButtonClick} startIcon={<Icon>send</Icon>} variant="contained">
                                    {t('component.CommentDialog.envia')}
                                </Button>
                            </Grid>
                        </Grid>
                    </MuiForm>
                </Grid>
            )}
        </Grid>
    );
};

export const useCommentDialog = (props: any = {}) => {
    const { readOnly, onClose } = props;
    const { t } = useBaseAppContext();
    // const apiRef = useMuiDataGridApiRef();
    const [dialogShow, dialogComponent] = useMuiContentDialog();

    const handleOpen = (id: any, name: string) => {
        dialogShow(
            t('component.CommentDialog.title', { name }),
            <Comments
                resourceName={'contingutComentariResource'}
                id={id}
                resourceReference={'contingut'}
                readOnly={readOnly}
                // i18nKeys={{
                //     createSuccess: 'page.expedient.action.comment.ok',
                // }}
            />,
            [],
            { maxWidth: 'md', fullWidth: true }
        ).then(
            () => onClose?.(),
            () => onClose?.()
        );
    };

    return {
        // apiRef,
        handleOpen,
        component: dialogComponent,
    };
};
